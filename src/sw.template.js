// Service worker : l'appli marche même sans réseau (la carte et les itinéraires, eux, en ont besoin)
const CACHE = 'jarvis-__VERSION__';
const SHELL = ['./', './index.html', './manifest.webmanifest', './lib/maplibre-gl.js', './lib/maplibre-gl.css', './icons/icon-192.png', './icons/icon-512.png'];
const TILES = 'jarvis-tiles';
self.addEventListener('install', e => { e.waitUntil(caches.open(CACHE).then(c => c.addAll(SHELL))); self.skipWaiting(); });
self.addEventListener('activate', e => {
  e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== CACHE && k !== TILES).map(k => caches.delete(k)))));
  self.clients.claim();
});
self.addEventListener('fetch', e => {
  const req = e.request; if (req.method !== 'GET') return;
  const u = new URL(req.url);
  if (u.origin === location.origin) { // nos fichiers : réseau d'abord pour avoir les mises à jour, cache si hors ligne
    e.respondWith(fetch(req).then(r => { const c = r.clone(); caches.open(CACHE).then(k => k.put(req, c)); return r; }).catch(() => caches.match(req)));
  } else if (/fonts\.(googleapis|gstatic)\.com$/.test(u.host)) {
    e.respondWith(caches.match(req).then(m => m || fetch(req).then(r => { const c = r.clone(); caches.open(CACHE).then(k => k.put(req, c)); return r; })));
  } else if (/^tiles\.openfreemap\.org$/.test(u.host)) { // tuiles de carte 3D : garde celles déjà vues pour les zones sans réseau
    e.respondWith(caches.open(TILES).then(k => k.match(req).then(m => {
      const net = fetch(req).then(r => { if (r.ok) k.put(req, r.clone()); return r; }).catch(() => m);
      return m || net;
    })));
  }
});
