rm -rf deploy/linked/client
rm -rf deploy/native_app_image/client
jlink --module-path build --add-modules vier.gewinnt --launcher VierGewinnt=vier.gewinnt/main.StartClient --output deploy/linked/client --strip-debug --no-header-files --no-man-pages
jpackage --name VierGewinnt --type app-image --module vier.gewinnt/main.StartClient --runtime-image deploy/linked/client --dest deploy/native_app_image/client