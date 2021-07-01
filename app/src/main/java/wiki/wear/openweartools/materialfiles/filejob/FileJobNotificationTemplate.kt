/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filejob

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.NotificationChannelTemplate
import wiki.wear.openweartools.materialfiles.util.NotificationTemplate

val fileJobNotificationTemplate: NotificationTemplate =
    NotificationTemplate(
        NotificationChannelTemplate(
            "file_job",
            R.string.notification_channel_file_job_name,
            NotificationManagerCompat.IMPORTANCE_LOW,
            descriptionRes = R.string.notification_channel_file_job_description,
            showBadge = false
        ),
        colorRes = R.color.color_primary,
        smallIcon = R.drawable.notification_icon,
        ongoing = true,
        category = NotificationCompat.CATEGORY_PROGRESS,
        priority = NotificationCompat.PRIORITY_LOW
    )
