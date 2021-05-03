package info.list.listview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {


    static class ModelEntity {

        @SerializedName("id")
        private int id;
        @SerializedName("title")
        private String name;

        @SerializedName("thumbnailUrl")
        private String imageURL;

        public ModelEntity(){

        }
        public ModelEntity(int id, String name
                ,
                           String imageURL
        )
        {
            this.id = id;
            this.name = name;
            this.imageURL = imageURL;

        }

        /*
         *GETTERS AND SETTERS
         */
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setimageURL(String name) {
            this.imageURL = name;
        }

        public String getImageURL() {
            return imageURL;
        }


        @Override
        public String toString() {
            return name;
        }
    }

    interface MyAPIService {

        @GET("/photos")
        Call<List<ModelEntity>> getModel();
    }

    static class RetrofitClientInstance {

        private static Retrofit retrofit;
        private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

        public static Retrofit getRetrofitInstance() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }

    class ListViewAdapter extends BaseAdapter{

        private List<ModelEntity> modelEntities;
        private Context context;

        public ListViewAdapter(Context context,List<ModelEntity> modelEntities){
            this.context = context;
            this.modelEntities = modelEntities;
        }

        @Override
        public int getCount() {
            return modelEntities.size();
        }

        @Override
        public Object getItem(int pos) {
            return modelEntities.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if(view==null)
            {
                view=LayoutInflater.from(context).inflate(R.layout.model,viewGroup,false);
            }

            TextView nameTxt = view.findViewById(R.id.nameTextView);


            ImageView modelImageView = view.findViewById(R.id.modelImageView);

            final ModelEntity thisModelEntity = modelEntities.get(position);

            nameTxt.setText(thisModelEntity.getName());



            if(thisModelEntity.getImageURL() != null && thisModelEntity.getImageURL().length()>0)
            {
                Picasso.get().load(thisModelEntity.getImageURL()).placeholder(R.drawable.placeholder).into(modelImageView);
            }else {
                Toast.makeText(context, "Empty Image URL", Toast.LENGTH_LONG).show();
                Picasso.get().load(R.drawable.placeholder).into(modelImageView);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, thisModelEntity.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }

    private ListViewAdapter adapter;
    private ListView mListView;


    private void populateListView(List<ModelEntity> modelEntityList) {

        mListView = findViewById(R.id.mListView);
        adapter = new ListViewAdapter(this, modelEntityList);
        mListView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressBar myProgressBar= findViewById(R.id.myProgressBar);
        myProgressBar.setIndeterminate(true);
        myProgressBar.setVisibility(View.VISIBLE);

        MyAPIService myAPIService = RetrofitClientInstance.getRetrofitInstance().create(MyAPIService.class);

        Call<List<ModelEntity>> call = myAPIService.getModel();
        call.enqueue(new Callback<List<ModelEntity>>() {

            @Override
            public void onResponse(Call<List<ModelEntity>> call, Response<List<ModelEntity>> response) {

                myProgressBar.setVisibility(View.GONE);

                populateListView(response.body());
            }
            @Override
            public void onFailure(Call<List<ModelEntity>> call, Throwable throwable) {
                myProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
