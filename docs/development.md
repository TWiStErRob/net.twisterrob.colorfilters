## IDE

Project is a standard Android Gradle project, so Android Studio or IntelliJ IDEA should work as usual.

## Task Collections
See also [CI setup](../.github/workflows/CI.yml).

### Full compilation
```
gradlew assemble :app:assembleDebugAndroidTest compileDebugUnitTestSources compileReleaseUnitTestSources compileDebugAndroidTestSources
```

### Static checks
```
gradlew detekt verifyReleaseResources lint violationReportHtml
```

### Run all UI tests
```
gradlew connectedCheck mergeAndroidReports --continue
```

### Rerun a UI test and re-merge
```
gradlew :feature:about:connectedCheck & gradlew -x connectedDebugAndroidTest mergeAndroidReports
```
