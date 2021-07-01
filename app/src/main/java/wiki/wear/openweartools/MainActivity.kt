package wiki.wear.openweartools

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.filelist.FileListActivity

class MainActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.main_activity)

        val materialFilesButton = findViewById<Button>(R.id.materialFilesButton)
        materialFilesButton.setOnClickListener { 
            startActivity(Intent(this, FileListActivity::class.java))
        }

        super.onCreate(savedInstanceState)
    }
}
