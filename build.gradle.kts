plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.zenyte"
version = "0.1.0-SNAPSHOT"

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(":lombok-edge")
    implementation(":RS2-Cache-Library")
    implementation(":toml4j-0.7.3-SNAPSHOT")
    implementation(":api-model-1.0")

    val lombok = "1.18.28"
    compileOnly("org.projectlombok:lombok:$lombok")
    annotationProcessor("org.projectlombok:lombok:$lombok")

    val slf4j = "2.0.7"
    implementation("org.slf4j:slf4j-api:$slf4j")
    implementation("org.slf4j:slf4j-simple:$slf4j")

    val log4j = "2.20.0"
    implementation("org.apache.logging.log4j:log4j-core:$log4j")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4j")

    val javacord = "3.4.0"
    implementation("org.javacord:javacord-api:$javacord")
    implementation("org.javacord:javacord-core:$javacord")

    val sdcf4j = "v1.0.10"
    implementation("de.btobastian.sdcf4j:sdcf4j-core:$sdcf4j")
    implementation("de.btobastian.sdcf4j:sdcf4j-javacord:$sdcf4j")

    implementation("pl.allegro.finance:tradukisto:1.12.0")
    implementation("io.netty:netty-all:4.1.93.Final")
    implementation("com.jolbox:bonecp:0.8.0.RELEASE")
    implementation("io.github.classgraph:classgraph:4.8.160")
    implementation("org.ow2.asm:asm:9.5")
    implementation("commons-io:commons-io:2.13.0")
    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("org.apache.commons:commons-pool2:2.11.1")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("com.esotericsoftware:kryo:5.5.0")
    implementation("com.mysql:mysql-connector-j:8.0.33")
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val defaultJvmArgs = arrayOf(
    //"-XX:+UseZGC",
    "-Xmx4g",
    "-Xms2g",
    "-XX:-OmitStackTraceInFastThrow",
    /*    "--add-opens=java.base/java.time=ALL-UNNAMED",
        "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED",
        "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED"*/
)

val defaultMainClassName = "com.zenyte.GameEngine"

fun execTask(
    name: String, mainClassName: String = defaultMainClassName, configure: (JavaExecSpec.() -> Unit)? = null
) = tasks.register(name, JavaExec::class.java) {
    group = ApplicationPlugin.APPLICATION_GROUP

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(mainClassName)
    jvmArgs(*defaultJvmArgs)

    enableAssertions = true
    if (hasProperty("args")) {
        val argsProperty = property("args")
        val argsList = argsProperty as List<*>
        if (argsList.isNotEmpty()) {
            args(argsList)
        }
    }

    configure?.invoke(this)
}

execTask("runMain") {
    args = listOf("main")
}

execTask("runOfflineDev") {
    args = listOf("offline_dev")
}

execTask("typeParser", "mgi.tools.parser.TypeParser") {
    args = listOf("--unzip")
}

execTask("mapPacker", "com.zenyte.game.util.MapPacker")

execTask("cachePacker", "com.zenyte.openrs.cache.CachePacking")

execTask("idTransform", "org.jire.zenytersps.idtransform.IDTransform")

application {
    mainClass.set(defaultMainClassName)
    applicationDefaultJvmArgs += defaultJvmArgs
}

tasks.shadowJar {
    isZip64 = true
}