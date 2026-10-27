#!/usr/bin/env python3
"""Render the in-app policy as a static, tracker-free public HTML page."""
from pathlib import Path
import html
root = Path(__file__).resolve().parents[1]
policy = (root/'app/src/main/assets/privacy-fr.txt').read_text()
sections = policy.strip().split('\n\n')
body = '\n'.join('<p>'+html.escape(p).replace('\n','<br>')+'</p>' for p in sections)
page = '''<!doctype html><html lang="fr"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Onde — Confidentialité</title><style>body{max-width:760px;margin:48px auto;padding:0 24px;font:17px/1.65 system-ui,sans-serif;color:#172438;background:#f8fafc}h1{line-height:1.2}p{white-space:normal;margin:24px 0}a{color:#265c99}</style><main><h1>Onde — Confidentialité</h1>'''+body+'''<p><a href="https://github.com/pulsarf0x/onde">Sources et assistance du projet Onde</a></p></main></html>'''
(root/'site/privacy.html').write_text(page)
(root/'site/index.html').write_text('''<!doctype html><html lang="fr"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Onde</title><main style="max-width:700px;margin:60px auto;padding:24px;font:18px/1.6 system-ui"><h1>Onde</h1><p>Une application Android indépendante pour transmettre le son autorisé des apps vers des enceintes AirPlay. Version de test : la compatibilité dépend du téléphone, du lecteur et du récepteur.</p><p><a href="privacy.html">Politique de confidentialité</a> · <a href="https://github.com/pulsarf0x/onde">Code source et assistance</a></p></main></html>''')
(root/'site/.nojekyll').touch()
