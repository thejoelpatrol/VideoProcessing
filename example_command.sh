time ffmpeg -i "/Volumes/Winchester Mystery House/Elgato captures/TAE BO LIVE! Sneak Preview WORKOUT.mp4" -vf scale=2*iw:2*ih -f image2pipe -vcodec ppm pipe:1 2>/dev/null | java -XX:MaxHeapFreeRatio=99 -Xverify:none -jar out/artifacts/PipeShift_jar/PipeShift.jar 5 | ffmpeg -framerate 30 -i pipe:0 -c:v libx265 -r 30 -crf 25 -pix_fmt yuv420p "/Volumes/Osteopathic Medicine/raw-video/tae-bo-5-scale-x265.mp4"
