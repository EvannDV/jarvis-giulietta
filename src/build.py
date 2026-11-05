#!/usr/bin/env python3
"""Construit la version installable (racine du dépôt) à partir de src/index.html.
Usage : python3 src/build.py   puis commit + push : GitHub Pages publie la racine."""
import pathlib
import time

SRC = pathlib.Path(__file__).resolve().parent
ROOT = SRC.parent

page = (SRC / 'index.html').read_text(encoding='utf-8')
version = time.strftime('%Y%m%d-%H%M%S')

head = '''<!doctype html>
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
window.JARVIS_HOSTED = true;
if ('serviceWorker' in navigator) addEventListener('load', () => navigator.serviceWorker.register('sw.js'));
</script>
</head>
<body>
'''

(ROOT / 'index.html').write_text(head + page + '\n</body>\n</html>\n', encoding='utf-8')
sw = (SRC / 'sw.template.js').read_text(encoding='utf-8').replace('__VERSION__', version)
(ROOT / 'sw.js').write_text(sw, encoding='utf-8')
print('Version installable construite :', version)
