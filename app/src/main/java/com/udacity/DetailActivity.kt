package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        var binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.lifecycleOwner = this

        val repoName = intent.getStringExtra("repo")
        val downloadStatus = intent.getStringExtra("downloadState")

        nameValueView.text = repoName
        nameValueView.setTextColor(Color.parseColor("#66FF66"))

        statusValueView.text = downloadStatus
        if(downloadStatus == "Successful"){
            statusValueView.setTextColor(Color.parseColor("#66FF66"))
        } else {
            statusValueView.setTextColor(Color.parseColor("#8B0000"))
        }
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        okBack.setOnClickListener {
            startActivity(mainIntent)
        }
    }

}
