/**
 * Created by shgarg on 26/03/18.
 */
package com.tag.photocaptureandgallery;


        import android.content.Context;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.net.Uri;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.takeimage.R;

        import java.io.IOException;
        import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter {

    ArrayList<String> personNames;
    ArrayList<Integer> personImages;
    ArrayList<String> audioPaths;
    Context context;

    public CustomAdapter(Context context, ArrayList personNames, ArrayList personImages, ArrayList audioPaths) {
        this.context = context;
        this.personNames = personNames;
        this.personImages = personImages;
        this.audioPaths = audioPaths;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
// infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
// set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
// set the data in items
        MyViewHolder holder1 = (MyViewHolder)holder;
        holder1.name.setText("Location:"+ GlobalMessages.locations.get(position));
        //holder1.image.setImageResource(personImages.get(position));

        holder1.image.setImageURI(Uri.parse(GlobalMessages.imagesPaths.get(position)));
        final String audioPath = GlobalMessages.audioPaths.get(position);
        holder1.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// open another activity on item click
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    //mediaPlayer.setDataSource(audioPaths.get(position));
                    if(audioPath != ""){
                        mediaPlayer.setDataSource(audioPath);
                        mediaPlayer.prepare();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(context, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });
// implement setOnClickListener event on item view.
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//// open another activity on item click
//                Intent intent = new Intent(context, SecondActivity.class);
//                intent.putExtra("image", personImages.get(position)); // put image data in Intent
//                context.startActivity(intent); // start Intent
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return GlobalMessages.imagesPaths.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        TextView name;
        ImageView image;
        Button playButton;


        public MyViewHolder(View itemView) {
            super(itemView);

// get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
            playButton = (Button) itemView.findViewById(R.id.audioPlay);

        }
    }
}
