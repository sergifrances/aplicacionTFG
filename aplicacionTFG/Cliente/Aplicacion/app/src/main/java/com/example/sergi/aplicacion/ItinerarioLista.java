package com.example.sergi.aplicacion;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;

public class ItinerarioLista extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerario_lista);

        ListView list = (ListView) findViewById(R.id.items);

        Road road = MainActivity.road; //too big to pass safely in Extras
        RoadNodesAdapter adapter = new RoadNodesAdapter(this, road);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
                Intent intent = new Intent();
                intent.putExtra("NODE_ID", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        list.setAdapter(adapter);

    }

    class RoadNodesAdapter extends BaseAdapter implements View.OnClickListener {
        private Context mContext;
        private Road mRoad;
        TypedArray iconIds;

        public RoadNodesAdapter(Context context, Road road) {
            mContext = context;
            mRoad = road;
            iconIds = mContext.getResources().obtainTypedArray(R.array.direction_icons);
        }

        @Override public int getCount() {
            if (mRoad == null || mRoad.mNodes == null)
                return 0;
            else
                return mRoad.mNodes.size();
        }

        @Override public Object getItem(int position) {
            if (mRoad == null || mRoad.mNodes == null)
                return null;
            else
                return mRoad.mNodes.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        //Mostrar datos del itinerario, como título, detalles e icono de cada acción
        @Override public View getView(int position, View convertView, ViewGroup viewGroup) {
            RoadNode entry = (RoadNode)getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.itinerario_item, null);
            }

            TextView tvTitle = (TextView)convertView.findViewById(R.id.title);
            String instructions = (entry.mInstructions==null ? "" : entry.mInstructions);
            tvTitle.setText("" + (position+1) + ". " + instructions);

            TextView tvDetails = (TextView)convertView.findViewById(R.id.details);
            tvDetails.setText(Road.getLengthDurationText(mContext, entry.mLength, entry.mDuration));

            int iconId = iconIds.getResourceId(entry.mManeuverType, R.mipmap.ic_empty);
            Drawable icon = mContext.getResources().getDrawable(iconId);
            ImageView ivManeuver = (ImageView)convertView.findViewById(R.id.thumbnail);
            ivManeuver.setImageDrawable(icon);

            return convertView;
        }

        @Override public void onClick(View arg0) {
            //nothing to do.
        }

    }
}
