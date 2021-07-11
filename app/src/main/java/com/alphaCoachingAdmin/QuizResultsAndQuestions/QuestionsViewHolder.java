package com.alphaCoachingAdmin.QuizResultsAndQuestions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoachingAdmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class QuestionsViewHolder extends RecyclerView.ViewHolder {

   public TextView question,opt1,opt2,opt3,opt4,correcttv,exptimetv;
  public Button edit,solution;
  public String questionID;
  public ImageView imageView;



    public QuestionsViewHolder(@NonNull View itemView) {
        super(itemView);
        question=itemView.findViewById(R.id.questionquiz);
        opt1=itemView.findViewById(R.id.opt1);
        opt2=itemView.findViewById(R.id.opt2);
        opt3=itemView.findViewById(R.id.opt3);
        opt4=itemView.findViewById(R.id.opt4);
        correcttv=itemView.findViewById(R.id.correctopt);
        exptimetv=itemView.findViewById(R.id.quetime);
        edit=itemView.findViewById(R.id.edit);
        solution=itemView.findViewById(R.id.solution);
        imageView=itemView.findViewById(R.id.questionimage);
//marks=itemView.findViewById(R.id.marks);







    }
    public void setImageView(String url){
        Log.d("IMAGES", "setImageView: ");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("imagesQuestion").child(url);
        storageReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
                Log.d("IMAGES", "onSuccess: Successs");
             //   holder.imageView.setImageBitmap(bitmap);
            }
        });

    }
}
