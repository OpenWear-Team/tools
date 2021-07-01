/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.ftpserver

import android.app.PendingIntent
import android.app.Service
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.app.NotificationIds
import wiki.wear.openweartools.materialfiles.util.NotificationChannelTemplate
import wiki.wear.openweartools.materialfiles.util.NotificationTemplate
import wiki.wear.openweartools.materialfiles.util.createIntent

val ftpServerServiceNotificationTemplate: NotificationTemplate =
    NotificationTemplate(
        NotificationChannelTemplate(
            "ftp_server",
            R.string.notification_channel_ftp_server_name,
            NotificationManagerCompat.IMPORTANCE_LOW,
            descriptionRes = R.string.notification_channel_ftp_server_description,
            showBadge = false
        ),
        colorRes = R.color.color_primary,
        smallIcon = R.drawable.notification_icon,
        contentTitleRes = R.string.ftp_server_notification_title,
        contentTextRes = R.string.ftp_server_notification_text,
        ongoing = true,
        category = NotificationCompat.CATEGORY_SERVICE,
        priority = NotificationCompat.PRIORITY_LOW
    )

object FtpServerServiceNotification {
    fun startForeground(service: Service) {
        val contentIntent = FtpServerActivity::class.createIntent()
        val contentPendingIntent = PendingIntent.getActivity(
            service, FtpServerActivity::class.hashCode(), contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = FtpServerReceiver.createIntent()
        val stopPendingIntent = PendingIntent.getBroadcast(
            service, FtpServerReceiver::class.hashCode(), stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = ftpServerServiceNotificationTemplate.createBuilder(service)
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.stop_icon_white_24dp, service.getString(R.string.stop),
                stopPendingIntent
            )
            .build()
        service.startForeground(NotificationIds.FTP_SERVER, notification)
    }

    fun stopForeground(service: Service) {
        service.stopForeground(true)
    }
}
