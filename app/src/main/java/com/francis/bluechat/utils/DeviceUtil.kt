package com.francis.bluechat.utils

import android.content.pm.PackageManager

fun PackageManager.missingSystemFeature(name: String) = !hasSystemFeature(name)