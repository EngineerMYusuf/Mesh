package myusuf.mesh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NodeSpecs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_specs);

        ImageView tempIMG = (ImageView) findViewById(R.id.imageView);
        tempIMG.setVisibility(View.INVISIBLE);
        ImageView humIMG = (ImageView) findViewById(R.id.imageView);
        humIMG.setVisibility(View.INVISIBLE);
        ImageView controlIMG = (ImageView) findViewById(R.id.imageView);
        controlIMG.setVisibility(View.INVISIBLE);
        ImageView unknownIMG = (ImageView) findViewById(R.id.imageView);
        unknownIMG.setVisibility(View.INVISIBLE);

        int num = 0;
        int myType = 0;
        String myData;
        num = getIntent().getIntExtra("WHICH_NODE",num);
        myType= getIntent().getIntExtra("NODE_TYPE",myType);
        myData= getIntent().getStringExtra("NODE_DATA");
        TextView name = (TextView) findViewById(R.id.nodeID);
        TextView type = (TextView) findViewById(R.id.nodeType);
        TextView data = (TextView) findViewById(R.id.nodeData);
        Log.d("progress","You want node: " + num );
        name.setText(String.valueOf(num));
        type.setText(String.valueOf(myType));
        data.setText(myData);
    }
}
