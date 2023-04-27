package dev.johnvinh.oneinthechamber

import java.io.File
import java.io.IOException

fun copyWorld(source: File, target: File) {
    if (!source.exists()) throw IOException("Source world not found")
    if (target.exists()) throw IOException("Target world already exists")
    source.copyRecursively(target)
}