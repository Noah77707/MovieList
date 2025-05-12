//Created by Noah Hoepfinger

package edu.msudenver.cs3013.movielist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Scanner
import android.view.Menu
import android.view.MenuItem



class MainActivity : AppCompatActivity() {
    val movieList: MutableList<Movie?> = ArrayList<Movie?>()
    val movieAdapter = MovieAdapter(movieList as MutableList<Movie>)
    var myPlace: String? = null


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            val title = data?.getStringExtra("title") ?: ""
            val year = data?.getStringExtra("year") ?: ""
            val genre = data?.getStringExtra("genre") ?: ""
            val rating = data?.getStringExtra("rating") ?: ""
            movieList.add(Movie(title, year, genre, rating))
            movieAdapter.notifyDataSetChanged()
        }

    fun readFile() {
        Log.d("PERSIST1", "readFile() entered")
        try {
            val f = File(myPlace, "/MOVIELIST.csv")
            Log.d("Movie", "File Path:" + f.absolutePath)
            f.createNewFile()
            val myReader = Scanner(f)
            while (myReader.hasNextLine()) {
                val data = myReader.nextLine()
                Log.d("PERSIST1", "LINE of input data: " + data)
                val parts = data.split(",")
                movieList.add(Movie(parts[0], parts[1], parts[2], parts[3]))
            }
            myReader.close()
        } catch (e: IOException) {
            Log.d("READ", "HIT EXCEP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + e)
            println("An error occurred.")
            e.printStackTrace()
        }
    }

    fun writeFile(v: View) {
        Log.d("PERSIST1", "writeFile() entered")
        try {
            val f = File(myPlace, "/MOVIELIST.csv")
            if (f.exists()) {
                Log.d("PERSIST1", "EXISTS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            } else {
                Log.d("PERSIST1", "DOES NOT EXIST >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            }
            val fw = FileWriter(f, false) // do not append =- over write
            val count = movieList.size
            Log.d("PERSIST1", "Count >>>>> = " + count + ">>>>>>>>>>>>>>>>>>>>>>>>>>")
// print the list
            for (i in 0..<count) { // note the cute Kotlin mechanism here ≤ .. <
                val s = movieList[i]?.convertOut() as String?
                Log.d("PERSIST1", s + ">>>>>>>>>>>>>>>>>>>>>>>>>>")
                fw.write(s + "\n")
            }
            fw.flush()
            fw.close()
        } catch (iox: IOException) {
            Log.d("PERSIST1", "HIT EXCEP >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            Log.d("PERSIST1", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EXCEP " + iox)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val myDir = this.getFilesDir()
        val myDirName = myDir.getAbsolutePath()
        myPlace = myDirName

        readFile()

        val recyclerView = findViewById<RecyclerView?>(R.id.recyclerView)
        recyclerView.setLayoutManager(LinearLayoutManager(this))

        val itemTouchHelper = ItemTouchHelper(movieAdapter.swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.setAdapter(movieAdapter)
    }



    fun startSecond(v: View) {
        startForResult.launch(Intent(this, AddMovieActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MOVIELIST", "options menu")
        when (item.itemId) {
            R.id.ratingSort -> {
                Log.d("MOVIELIST", "onOptions: rating sort")
                movieList?.sortBy{ it?.rating }
                movieAdapter.notifyDataSetChanged()
            }
            R.id.yearSort -> {
                Log.d("MOVIELIST", "onOptions: year sort")
                movieList?.sortBy{ it?.year }
                movieAdapter.notifyDataSetChanged()
            }
            R.id.genreSort -> {
                Log.d("MOVIELIST", "onOptions: genre sort")
                movieList?.sortBy{ it?.genre }
                movieAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }





}