#!/usr/bin/env bash
# Prepare a portable Maven repository for USB / offline use.
# Run this ONCE while you still have internet.

set -euo pipefail
cd "$(dirname "$0")/.."

MVN=mvn
if [[ -x "$PWD/.tools/apache-maven-3.9.11/bin/mvn" ]]; then
  MVN="$PWD/.tools/apache-maven-3.9.11/bin/mvn"
fi

echo "Using Maven: $MVN"
echo "[1/3] Resolving project dependencies..."
"$MVN" -U dependency:resolve

echo "[2/3] Resolving plugins..."
"$MVN" -U dependency:resolve-plugins

echo "[3/3] Packaging WAR to verify and warm caches..."
"$MVN" -U -DskipTests clean package

echo
echo "SUCCESS."
echo "Copy this project folder AND your local Maven repository to the USB:"
echo "  Project : $(pwd)"
echo "  Maven   : ${HOME}/.m2/repository"
echo
echo "On the offline PC:"
echo "  mvn -o -Dmaven.repo.local=/path/to/usb/m2-repository clean package"
echo
echo "See how-to-run.md for full offline instructions."
