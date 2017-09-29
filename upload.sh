chmod 777 gradlew
./push.sh
./gradlew assembleRelease
curl -F "file=@./app/build/outputs/apk/app-release.apk" -F "uKey=f16960d4b0e9a600078bb791fb43912b" -F "_api_key=9454aabcc1e12da0a9a33e958d7541ae" http://www.pgyer.com/apiv1/app/upload
