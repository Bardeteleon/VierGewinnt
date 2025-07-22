rm -rf deploy/linked/server
rm -rf deploy/native_app_image/server
jlink --module-path build --add-modules vier.gewinnt --launcher VierGewinntServer=vier.gewinnt/main.StartServer --output deploy/linked/server --strip-debug --no-header-files --no-man-pages
jpackage --name VierGewinntServer --type app-image --module vier.gewinnt/main.StartServer --runtime-image deploy/linked/server --dest deploy/native_app_image/server