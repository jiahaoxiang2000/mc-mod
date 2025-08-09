# Release Instructions for Isomo Mod v1.0.0

## GitHub Actions Automated Release Setup ✅

The repository is now configured with automated GitHub Actions for releases. Here's what has been set up:

### Files Created:
- `.github/workflows/release.yml` - Main release workflow
- Updated `CHANGELOG.md` with v1.0.0 section
- Added version links in changelog

### How the Automated Release Works:

1. **Trigger**: When you create and push a tag like `v1.0.0`
2. **Build Process**: 
   - Checks out code
   - Sets up Java 17 environment
   - Caches Gradle dependencies
   - Builds the mod with `./gradlew build`
   - Finds the generated JAR file (`isomomod-1.0.0.jar`)
3. **Release Creation**:
   - Extracts changelog content for the specific version
   - Creates GitHub release with version number as title
   - Uses changelog content as release description
   - Uploads the built JAR as a release asset

### To Release v1.0.0:

1. **Commit all changes**:
   ```bash
   git add .
   git commit -m "🎉 release: prepare v1.0.0 with GitHub Actions automation"
   ```

2. **Create and push tag**:
   ```bash
   git tag v1.0.0
   git push origin main
   git push origin v1.0.0
   ```

3. **Automatic Process**:
   - GitHub Actions will trigger automatically
   - Build will run (~2-3 minutes)
   - Release will be created with:
     - Title: "Isomo Mod 1.0.0"
     - Description: Full changelog from v1.0.0 section
     - Asset: `isomomod-1.0.0.jar` (ready to download)

### What's Included in v1.0.0:

✨ **New Features:**
- Left-click block deletion with same patterns as placement
- Configurable pattern sizes (1-50 blocks) via commands
- 90-degree pattern rotation with 'R' key
- Runtime configuration system for colors and settings
- Extended block placement beyond vanilla reach
- Mouse wheel pattern switching
- Comprehensive command system

🔄 **Changes:**
- Dynamic wireframe colors instead of static constants
- Modern CHANGELOG.md format
- Updated project structure

🐛 **Fixes:**
- Fixed event bus registration errors
- Fixed configurable reach distance handling

### After Release:

The GitHub release will be created automatically at:
`https://github.com/YOUR_USERNAME/YOUR_REPO/releases/tag/v1.0.0`

Users can download the `isomomod-1.0.0.jar` file directly from the release page.

### For Future Releases:

1. Add new features to the `[Unreleased]` section in CHANGELOG.md
2. When ready to release, move content to a new versioned section (e.g., `[1.1.0] - YYYY-MM-DD`)
3. Update version in `gradle.properties`
4. Create and push the version tag
5. GitHub Actions will handle the rest automatically!

---

**Note**: Make sure to update the GitHub repository URLs in `CHANGELOG.md` version links section with your actual username/repository name before creating the tag.