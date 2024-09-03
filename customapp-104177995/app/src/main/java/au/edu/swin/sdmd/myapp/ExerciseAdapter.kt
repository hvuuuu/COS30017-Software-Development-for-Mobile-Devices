package au.edu.swin.sdmd.myapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseAdapter(private val exerciseList: MutableList<Exercise>, private val editExercise: (Exercise, Int) -> Unit) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val db = FirebaseFirestore.getInstance() // Initialize Firestore instance

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.exercise) // Find the title TextView
        val date: TextView = itemView.findViewById(R.id.date) // Find the date TextView
        val duration: TextView = itemView.findViewById(R.id.duration) // Find the duration TextView
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete) // Find the delete button
        val editButton: ImageButton = itemView.findViewById(R.id.edit) // Find the edit button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        // Inflate the item layout and create a ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exercise_item_cell, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val currentItem = exerciseList[position] // Get the current exercise item
        holder.title.text = currentItem.title // Set the title text
        holder.date.text = currentItem.date // Set the date text
        holder.duration.text = currentItem.duration // Set the duration text

        // Set the click listener for the delete button
        holder.deleteButton.setOnClickListener {
            deleteExercise(holder.adapterPosition, holder) // Delete the exercise at the holder's position
        }

        // Set the click listener for the edit button
        holder.editButton.setOnClickListener {
            editExercise(currentItem, holder.adapterPosition) // Trigger the edit action with the current item and position
        }
    }

    override fun getItemCount() = exerciseList.size // Return the size of the exercise list

    fun addExercise(exercise: Exercise) {
        exerciseList.add(exercise) // Add the new exercise to the list
        notifyItemInserted(exerciseList.size - 1) // Notify that a new item was inserted
    }

    fun updateExercise(exercise: Exercise, position: Int) {
        exerciseList[position] = exercise // Update the exercise at the specified position
        notifyItemChanged(position) // Notify that the item has changed
    }

    fun deleteExercise(position: Int, holder: ExerciseViewHolder) {
        val exercise = exerciseList[position] // Get the exercise to delete
        db.collection("exercises").document(exercise.id.toString())
            .delete() // Delete the exercise document from Firestore
            .addOnSuccessListener {
                exerciseList.removeAt(position) // Remove the exercise from the list
                notifyItemRemoved(position) // Notify that an item was removed
                Toast.makeText(holder.itemView.context, "Exercise deleted successfully", Toast.LENGTH_SHORT).show() // Show success message
                // Fetch exercises again
                (holder.itemView.context as MainActivity).fetchExercises() // Call fetchExercises() from MainActivity to update the list
            }
            .addOnFailureListener { e ->
                Log.w("ExerciseAdapter", "Error deleting document", e) // Log any errors
                Toast.makeText(holder.itemView.context, "Failed to delete exercise: ${e.message}", Toast.LENGTH_SHORT).show() // Show failure message
            }
    }

    fun updateExercises(exercises: List<Exercise>) {
        exerciseList.clear() // Clear the current list
        exerciseList.addAll(exercises) // Add all new exercises
        notifyDataSetChanged() // Notify that the dataset has changed
    }
}
