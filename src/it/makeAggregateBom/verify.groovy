void assertBomFiles(String path) {
    File bomFileXml = new File(basedir, path + ".xml")
    File bomFileJson = new File(basedir, path + ".json")

    assert bomFileXml.exists()
    assert bomFileJson.exists()
}

assertBomFiles("target/bom") // aggregate
assertBomFiles("api/target/bom")
assertBomFiles("util/target/bom")
assertBomFiles("impls/target/bom")
assertBomFiles("impls/impl-A/target/bom")
assertBomFiles("impls/impl-B/target/bom")

var buildLog = new File(basedir, "build.log").text

// 13 = 6 modules for main cyclonedx-makeAggregateBom execution
//    + 1 for root module cyclonedx-makeAggregateBom-root-only execution
//    + 6 modules for additional cyclonedx-makeBom execution
assert 13 == (buildLog =~ /\[INFO\] CycloneDX: Writing BOM \(XML\)/).size()
assert 13 == (buildLog =~ /\[INFO\] CycloneDX: Validating BOM \(XML\)/).size()
assert 13 == (buildLog =~ /\[INFO\] CycloneDX: Writing BOM \(JSON\)/).size()
assert 13 == (buildLog =~ /\[INFO\] CycloneDX: Validating BOM \(JSON\)/).size()
// cyclonedx-makeAggregateBom-root-only execution skips 5 non-root modules
assert 5 == (buildLog =~ /\[INFO\] Skipping aggregate CycloneDX on non-execution root/).size()

// [WARNING] artifact org.cyclonedx.its:api:xml:cyclonedx:1.0-SNAPSHOT already attached, replace previous instance
assert 0 == (buildLog =~ /-SNAPSHOT already attached, replace previous instance/).size()

String cleanBom(String path) {
    File bomFile = new File(basedir, path)
    return bomFile.text.replaceFirst(/urn:uuid:........-....-....-....-............/, "urn:uuid:").replaceFirst(/\d{4}-\d\d-\d\dT\d\d:\d\d:\d\dZ/, "");
}

void assertBomEqualsNonAggregate(String path) {
    String bomXml = cleanBom(path + "-makeBom.xml")
    String aggregateBomXml = cleanBom(path + ".xml")
    assert bomXml == aggregateBomXml

    String bomJson = cleanBom(path + "-makeBom.json")
    String aggregateBomJson = cleanBom(path + ".json")
    assert bomJson == aggregateBomJson
}

assertBomEqualsNonAggregate("api/target/bom")
assertBomEqualsNonAggregate("util/target/bom")
assertBomEqualsNonAggregate("impls/target/bom")
assertBomEqualsNonAggregate("impls/impl-A/target/bom")
assertBomEqualsNonAggregate("impls/impl-B/target/bom")
