package com.kaidoh.mayuukhvarshney.zenmode;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * Created by mayuukhvarshney on 16/10/16.
 */
public class TimeLine extends Fragment {

    protected ZenAdapter zenAdapter;
    protected ArrayList<DATA> ZENDATA;
    protected ArrayList<DATA> PurifiedZen;
    protected HashMap<String,Integer>Remover;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState)
{
    view = inflater.inflate(R.layout.database_list,container,false);
    ZenDataBase db = new ZenDataBase(getActivity());
    Remover = new HashMap<>();

   ZENDATA = new ArrayList<>();
    PurifiedZen = new ArrayList<>();

    ZENDATA = db.getInfo();
for (DATA i : ZENDATA)
{
     if(i.getZenHours()!=null && i.getMoveHours()!=null)
     {

         if(!Remover.containsKey(i.getDate()) && !(i.getZenHours().equals("0")) && !(i.getMoveHours()).equals("0"))
         {
             Remover.put(i.getDate(),1);
             PurifiedZen.add(i);
         }
     }
}
    View recyclerView = view.findViewById(R.id.data_list);
    assert recyclerView != null;
    setupRecyclerView((RecyclerView) recyclerView);

    return view;
}
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        zenAdapter= new ZenAdapter(PurifiedZen);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(zenAdapter);
        zenAdapter.notifyDataSetChanged();


    }



    public class ZenAdapter extends RecyclerView.Adapter<ZenAdapter.MyViewHolder> {

        private  ArrayList<DATA> ZEN_DATA;
        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            public TextView ZenHours,MovementHours,DATE;


            public MyViewHolder(View view)
            {
                super(view);
                ZenHours = (TextView) view.findViewById(R.id.ZenHours);
                MovementHours = (TextView) view.findViewById(R.id.MovementHours);
                DATE  = (TextView) view.findViewById(R.id.Date);

            }
        }
        public ZenAdapter(ArrayList<DATA> data){
            this.ZEN_DATA = data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.database_row_content, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

                    holder.ZenHours.setText(ZEN_DATA.get(position).getZenHours());

                    holder.MovementHours.setText(ZEN_DATA.get(position).getMoveHours());

                holder.DATE.setText(ZEN_DATA.get(position).getDate());


        }

        @Override
        public int getItemCount() {
            return ZEN_DATA.size();
        }
    }
    public void Reload()
{

    ZenDataBase db = new ZenDataBase(getActivity());
    ZENDATA = db.getInfo();
    for (DATA i : ZENDATA)
    {
        if(i.getZenHours()!=null && i.getMoveHours()!=null)
        {

            if(!Remover.containsKey(i.getDate()) && !(i.getZenHours().equals("0")) && !(i.getMoveHours()).equals("0"))
            {
                Remover.put(i.getDate(),1);
                PurifiedZen.add(i);
            }
        }
    }
    View recyclerView = view.findViewById(R.id.data_list);
    assert recyclerView != null;
    setupRecyclerView((RecyclerView) recyclerView);
}

}
