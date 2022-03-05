### Full compilation
```
gradlew assemble :app:assembleDebugAndroidTest compileDebugUnitTestSources compileReleaseUnitTestSources compileDebugAndroidTestSources
```

### Static checks
```
gradlew verifyReleaseResources lint violationReportHtml
```

### Run all UI tests
```
gradlew connectedCheck mergeAndroidReports --continue
```

### Rerun a UI test and re-merge
```
gradlew :feature:about:connectedCheck & gradlew -x connectedDebugAndroidTest mergeAndroidReports
```
