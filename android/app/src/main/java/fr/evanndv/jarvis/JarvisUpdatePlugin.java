package fr.evanndv.jarvis;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Mise à jour de Jarvis depuis l'appli : télécharge le dernier APK publié sur GitHub
 * puis ouvre l'installateur d'Android (l'utilisateur confirme toujours l'installation).
 * Événement JS : "progress" { pct }.
 */
@CapacitorPlugin(name = "JarvisUpdate")
public class JarvisUpdatePlugin extends Plugin {

    private boolean canInstall() {
        return Build.VERSION.SDK_INT < 26 || getContext().getPackageManager().canRequestPackageInstalls();
    }

    private void openInstallSettings() {
        if (Build.VERSION.SDK_INT < 26) return;
        Intent i = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getContext().getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
    }

    @PluginMethod
    public void canInstall(PluginCall call) {
        JSObject o = new JSObject();
        o.put("value", canInstall());
        call.resolve(o);
    }

    @PluginMethod
    public void openInstallSettings(PluginCall call) {
        openInstallSettings();
        call.resolve();
    }

    @PluginMethod
    public void install(PluginCall call) {
        final String url = call.getString("url");
        if (url == null) { call.reject("Adresse de l'APK manquante"); return; }
        if (!canInstall()) {
            openInstallSettings();
            call.reject("Autorisez Jarvis à installer des applis, puis réessayez", "permission");
            return;
        }
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                File dir = new File(getContext().getCacheDir(), "updates");
                if (!dir.exists() && !dir.mkdirs()) throw new Exception("dossier de téléchargement impossible");
                File f = new File(dir, "Jarvis.apk");
                // GitHub redirige vers un autre domaine : on suit les redirections nous-mêmes
                String next = url;
                for (int hop = 0; hop < 6; hop++) {
                    c = (HttpURLConnection) new URL(next).openConnection();
                    c.setInstanceFollowRedirects(false);
                    c.setConnectTimeout(15000);
                    c.setReadTimeout(30000);
                    c.setRequestProperty("Accept", "application/octet-stream");
                    int code = c.getResponseCode();
                    if (code >= 300 && code < 400 && c.getHeaderField("Location") != null) {
                        next = new URL(new URL(next), c.getHeaderField("Location")).toString();
                        c.disconnect();
                        continue;
                    }
                    if (code != 200) throw new Exception("HTTP " + code);
                    break;
                }
                long total = c.getContentLengthLong(), done = 0, lastEmit = 0;
                try (InputStream in = c.getInputStream(); FileOutputStream out = new FileOutputStream(f)) {
                    byte[] buf = new byte[65536];
                    int n;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                        done += n;
                        long now = System.currentTimeMillis();
                        if (total > 0 && now - lastEmit > 250) {
                            lastEmit = now;
                            JSObject p = new JSObject();
                            p.put("pct", (int) (done * 100 / total));
                            notifyListeners("progress", p);
                        }
                    }
                }
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(uri, "application/vnd.android.package-archive");
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
                JSObject r = new JSObject();
                r.put("bytes", done);
                call.resolve(r);
            } catch (Exception e) {
                call.reject("Téléchargement impossible : " + e.getMessage());
            } finally {
                if (c != null) c.disconnect();
            }
        }).start();
    }
}
