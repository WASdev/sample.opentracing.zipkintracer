# Migration Guide: OSSRH to Maven Central Portal

## Overview

This project has been migrated from the legacy OSSRH (OSS Repository Hosting) to the new **Maven Central Portal** for publishing releases to Maven Central.

## What Changed

### 1. Distribution Management
- **Old URL**: `https://oss.sonatype.org/service/local/staging/deploy/maven2/`
- **New URL**: `https://central.sonatype.com/api/v1/publisher`
- **Repository ID**: Changed from `sonatype-nexus-staging` to `central`

### 2. Publishing Plugin
Added the `central-publishing-maven-plugin` configured for **manual publishing**:
- Artifacts are deployed to Central Portal staging area
- You manually review and publish via web UI
- Provides safety net to drop bad deployments

### 3. Release Profile
- **Old Profile**: `sonatype-oss-release`
- **New Profile**: `central-release`
- Updated plugin versions for better compatibility
- Same functionality: GPG signing, source, and javadoc attachment

## Prerequisites

### 1. Maven Central Portal Account
1. Create an account at https://central.sonatype.com/
2. Verify your namespace (e.g., `net.wasdev.wlp.tracer`)
3. Generate a user token for authentication

### 2. Update Maven Settings
Update your `~/.m2/settings.xml` with Central Portal credentials:

```xml
<settings>
    <servers>
        <server>
            <id>central</id>
            <username>YOUR_CENTRAL_PORTAL_USERNAME</username>
            <password>YOUR_CENTRAL_PORTAL_TOKEN</password>
        </server>
    </servers>
</settings>
```

**To generate credentials:**
1. Log in to https://central.sonatype.com/
2. Click on your profile (top right)
3. Select "View Account"
4. Click "Generate User Token"
5. Copy the generated username and password to your settings.xml

### 3. GPG Key Setup
Ensure you have a GPG key configured for signing artifacts:

```bash
# List existing keys
gpg --list-keys

# Generate a new key if needed
gpg --gen-key

# Publish your public key to a keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

## New Publishing Workflow

### Step 1: Prepare Release
Ensure your code is ready and all tests pass:

```bash
mvn clean verify -P central-release
```

This will:
- Compile the code
- Run tests
- Generate source and javadoc JARs
- Sign all artifacts with GPG

### Step 2: Deploy to Central Portal
Deploy artifacts to the Central Portal staging area:

```bash
mvn clean deploy -P central-release
```

**What happens:**
- Artifacts are uploaded to Central Portal
- Deployment is created in "PENDING" state
- Nothing is published to Maven Central yet

### Step 3: Review in Web UI
1. Log in to https://central.sonatype.com/
2. Navigate to "Deployments" in the left sidebar
3. Find your deployment (sorted by date, most recent first)
4. Click on the deployment to review

**Artifacts to verify:**
- ✅ Main JAR: `liberty-opentracing-zipkintracer-{version}.jar`
- ✅ Sources JAR: `liberty-opentracing-zipkintracer-{version}-sources.jar`
- ✅ Javadoc JAR: `liberty-opentracing-zipkintracer-{version}-javadoc.jar`
- ✅ POM file: `liberty-opentracing-zipkintracer-{version}.pom`
- ✅ GPG signatures: `.asc` files for each artifact

**Check for:**
- Correct version number
- All required artifacts present
- Valid GPG signatures
- Correct POM metadata

### Step 4: Publish or Drop

#### Option A: Publish (Release to Maven Central)
If everything looks good:
1. Click the "Publish" button
2. Confirm the action
3. Wait for processing (usually 15-30 minutes)
4. Verify on Maven Central: https://central.sonatype.com/artifact/net.wasdev.wlp.tracer/liberty-opentracing-zipkintracer

**Note:** Publishing is **irreversible**. Once published, artifacts cannot be deleted from Maven Central.

#### Option B: Drop (Delete Deployment)
If you find issues:
1. Click the "Drop" button
2. Confirm the action
3. Fix the issues in your code
4. Deploy again (repeat from Step 2)

### Step 5: Verify Publication
After publishing, verify the release:

1. **Central Portal Search:**
   - https://central.sonatype.com/artifact/net.wasdev.wlp.tracer/liberty-opentracing-zipkintracer

2. **Maven Central Search:**
   - https://search.maven.org/artifact/net.wasdev.wlp.tracer/liberty-opentracing-zipkintracer

3. **Direct Repository Access:**
   - https://repo1.maven.org/maven2/net/wasdev/wlp/tracer/liberty-opentracing-zipkintracer/

**Note:** It may take 15-30 minutes for artifacts to appear on Maven Central after publishing.

## Comparison: Old vs New Process

### Old Process (OSSRH)
```bash
# Deploy to OSSRH
mvn clean deploy -P sonatype-oss-release

# Then manually:
# 1. Log in to https://oss.sonatype.org/
# 2. Navigate to Staging Repositories
# 3. Find your repository
# 4. Close the repository
# 5. Wait for validation
# 6. Release the repository
```

### New Process (Central Portal)
```bash
# Deploy to Central Portal
mvn clean deploy -P central-release

# Then manually:
# 1. Log in to https://central.sonatype.com/
# 2. Navigate to Deployments
# 3. Review your deployment
# 4. Click "Publish" (or "Drop" if issues found)
```

## Benefits of Central Portal

1. **Simpler UI**: Modern, intuitive interface
2. **Faster Publishing**: Streamlined process, faster propagation
3. **Better Review**: Clearer artifact listing and validation
4. **API Access**: RESTful API for automation (if needed later)
5. **Token Authentication**: More secure than password-based auth
6. **Future-Proof**: Central Portal is the future of Maven Central publishing

## Troubleshooting

### Issue: "401 Unauthorized" during deployment
**Solution:** Check your Maven settings.xml:
- Ensure server ID is `central` (not `sonatype-nexus-staging`)
- Verify username and token are correct
- Regenerate token if needed

### Issue: "403 Forbidden" or namespace verification error
**Solution:** Verify your namespace ownership:
1. Log in to https://central.sonatype.com/
2. Navigate to "Namespaces"
3. Ensure `net.wasdev.wlp.tracer` is verified
4. Follow verification instructions if needed

### Issue: GPG signing fails
**Solution:** Check GPG configuration:
```bash
# Verify GPG is installed
gpg --version

# List available keys
gpg --list-secret-keys

# Test signing
echo "test" | gpg --clearsign
```

If using GPG agent, ensure it's running:
```bash
gpg-agent --daemon
```

### Issue: Missing artifacts in deployment
**Solution:** Ensure the profile is activated:
```bash
mvn clean deploy -P central-release -X
```

Check the debug output (`-X`) for plugin execution details.

### Issue: Deployment stuck in "PENDING" state
**Solution:** 
- Wait a few minutes for processing
- Refresh the page
- Check for validation errors in the deployment details
- If stuck for >10 minutes, drop and redeploy

## Rolling Back (Emergency)

If you need to temporarily revert to OSSRH:

1. **Restore old distributionManagement:**
```xml
<distributionManagement>
    <repository>
        <id>sonatype-nexus-staging</id>
        <name>Nexus Release Repository</name>
        <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
</distributionManagement>
```

2. **Remove central-publishing-maven-plugin** from pom.xml

3. **Restore old profile name** (optional):
   - Change `central-release` back to `sonatype-oss-release`

4. **Update settings.xml** with OSSRH credentials

## Support and Resources

- **Central Portal Documentation**: https://central.sonatype.org/publish/publish-portal-maven/
- **Central Portal UI**: https://central.sonatype.com/
- **Maven Central Search**: https://search.maven.org/
- **Support**: https://central.sonatype.org/support/

## Migration Checklist

- [x] Updated pom.xml with Central Portal configuration
- [x] Added central-publishing-maven-plugin
- [x] Updated release profile to central-release
- [ ] Created Central Portal account
- [ ] Verified namespace ownership
- [ ] Generated user token
- [ ] Updated Maven settings.xml
- [ ] Tested deployment to Central Portal
- [ ] Successfully published first release via new process

## Notes

- The manual publishing approach provides maximum control and safety
- You can switch to automated publishing later by setting `<autoPublish>true</autoPublish>`
- Keep your user token secure - treat it like a password
- GPG signatures are required for all artifacts
- All metadata (licenses, SCM, developers) must be present in POM

---

**Migration Date**: January 2026  
**Migrated By**: Felix Wong  
**Central Portal Version**: 0.6.0