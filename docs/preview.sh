#!/usr/bin/env bash
BASEDIR=$(dirname "$0")
cd "$BASEDIR" || exit 1
python3 -m venv .venv
. .venv/bin/activate
pip install -r source/requirements.txt
make clean html
xdg-open "file://$(pwd)/build/html/index.html"
