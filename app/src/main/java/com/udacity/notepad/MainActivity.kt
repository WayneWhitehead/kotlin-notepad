package com.udacity.notepad

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.notepad.crud.CreateActivity
import com.udacity.notepad.databinding.ActivityMainBinding
import com.udacity.notepad.recycler.NotesAdapter
import com.udacity.notepad.util.SpaceItemDecoration

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener { startActivity(CreateActivity.get(this@MainActivity)) }

        binding.content.recycler.setLayoutManager(LinearLayoutManager(this))
        binding.content.recycler.addItemDecoration(SpaceItemDecoration(this, R.dimen.margin_small))
        binding.content.recycler.setAdapter(NotesAdapter(this))
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.content.recycler.setAdapter(null)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        (binding.content.recycler.adapter as NotesAdapter?)!!.refresh()
    }
}
