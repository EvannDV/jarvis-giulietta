#!/usr/bin/env python3
"""Construit, à partir de src/index.html :
- la version web installable (racine du dépôt, publiée par GitHub Pages) ;
- la version de l'appli Android (dossier www/, emballé par Capacitor).
Usage : python3 src/build.py"""
import os
import pathlib
import shutil
import time

SRC = pathlib.Path(__file__).resolve().parent
ROOT = SRC.parent

page = (SRC / 'index.html').read_text(encoding='utf-8')
version = time.strftime('%Y%m%d-%H%M%S')

def head(native):
    build = os.environ.get('GITHUB_RUN_NUMBER', '0')  # numéro de build GitHub Actions = versionCode de l'APK
    sha = os.environ.get('GITHUB_SHA', '')[:7]
    stamp = f"window.JARVIS_BUILD = {int(build)}; window.JARVIS_SHA = '{sha}';"
    boot = (stamp + " window.JARVIS_HOSTED = true; window.JARVIS_NATIVE = true;" if native else
            "window.JARVIS_HOSTED = true;\nif ('serviceWorker' in navigator) addEventListener('load', () => navigator.serviceWorker.register('sw.js'));")
    return '''<!doctype html>
<html lang="fr">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<meta name="theme-color" content="#02070c">
<meta name="mobile-web-app-capable" content="yes">
<link rel="manifest" href="manifest.webmanifest">
<link rel="icon" href="icons/icon-192.png">
<link rel="apple-touch-icon" href="icons/icon-192.png">
<style>:root{padding-top:env(safe-area-inset-top,0px);padding-bottom:env(safe-area-inset-bottom,0px)}body{margin:0}[hidden]{display:none!important}</style>
<script>
__BOOT__
</script>
</head>
<body>
'''.replace('__BOOT__', boot)

(ROOT / 'index.html').write_text(head(False) + page + '\n</body>\n</html>\n', encoding='utf-8')
sw = (SRC / 'sw.template.js').read_text(encoding='utf-8').replace('__VERSION__', version)
(ROOT / 'sw.js').write_text(sw, encoding='utf-8')
# Appli Android : mêmes fichiers, sans service worker (tout est déjà dans l'APK)
www = ROOT / 'www'
shutil.rmtree(www, ignore_errors=True)
www.mkdir()
(www / 'index.html').write_text(head(True) + page + '\n</body>\n</html>\n', encoding='utf-8')
for d in ('lib', 'icons'):
    shutil.copytree(ROOT / d, www / d)
shutil.copy(ROOT / 'manifest.webmanifest', www / 'manifest.webmanifest')
print('Version web + appli Android construites :', version)
