#!/bin/bash

echo "🏄‍♂️ Setting up Carpet Surfer repository..."

# Create directory structure
mkdir -p .github/workflows
mkdir -p app/src/main/assets/www/videos
mkdir -p app/src/main/java/com/carpetsurfer/app
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/values-night
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi
mkdir -p app/src/main/res/mipmap-anydpi-v26
mkdir -p app/src/main/res/xml
mkdir -p gradle/wrapper

echo "✅ Directory structure created!"
echo ""
echo "Next steps:"
echo "1. Copy your video files to app/src/main/assets/www/videos/"
echo "   - Rename 'grok_video_2026-02-17-13-22-33(1).mp4' to 'intro.mp4'"
echo "   - Rename 'grok_video_2026-02-17-13-31-06(1).mp4' to 'dope-scope.mp4'"
echo ""
echo "2. Push to GitHub:"
echo "   git init"
echo "   git add ."
echo "   git commit -m 'Initial commit'"
echo "   git remote add origin https://github.com/YOUR_USERNAME/carpet-surfer.git"
echo "   git push -u origin main"
echo ""
echo "3. GitHub Actions will automatically build your APK!"
echo ""
echo "4. Download APK from Actions tab"
