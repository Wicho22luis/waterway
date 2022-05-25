package com.example.waterway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //LINE DATA VAR
    LineChart waterQtChart;
    ArrayList<Entry> dataVal=new ArrayList<Entry>();
    ArrayList<BarEntry> dataBarVal=new ArrayList<BarEntry>();
    ArrayList<String> lineChartLabels=new ArrayList<String>();
    //PH BARCHART
    BarChart phBarChart;
    //FIREBASE DATA
    DatabaseReference motorReference=FirebaseDatabase.getInstance().getReference();
    DatabaseReference cardsReference=FirebaseDatabase.getInstance().getReference();
    DatabaseReference chartReference=FirebaseDatabase.getInstance().getReference();
    //TARJETAS
    LinearLayout motorCardParent,cardsParent,motorCardRed,motorCardBlue;
    TextView txtLevel,txtSolids,txtPh;
    int bombaStatus=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //INICIALIZACION DE VISTAS
        waterQtChart=findViewById(R.id.waterQt_Chart);
        phBarChart=findViewById(R.id.phBarChart);
        motorCardParent=findViewById(R.id.motorCardParent);
        cardsParent=findViewById(R.id.cardParent);
        motorCardBlue=findViewById(R.id.motorVariantBlue);
        motorCardRed=findViewById(R.id.motorVariantRed);
        txtLevel=findViewById(R.id.txtLevel);
        txtPh=findViewById(R.id.txtPh);
        txtSolids=findViewById(R.id.txtSolids);
        dataValues();
        initBarData();
        initFirebaseData();
        motorCardParent.setOnClickListener(v -> operarBomba());

        cardsParent.setAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_animation));

    }
    
    //LINE DATA SET
    private void dataValues(){
        chartReference.child("WATER_FLOW").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cont=0;
                dataVal.removeAll(dataVal);
                lineChartLabels.removeAll(lineChartLabels);
                for (DataSnapshot data:
                        snapshot.getChildren()){
                dataVal.add(new Entry(cont,data.getValue(Float.class)));
                lineChartLabels.add(data.getKey());
                    //Toast.makeText(MainActivity.this, "Dato: "+data.getValue(Float.class)+" KEY: "+data.getKey(), Toast.LENGTH_SHORT).show();
                cont++;
                }
                initLineChartData(dataVal);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void initLineChartData(ArrayList<Entry> dataValues1){
        LineDataSet lineDataSet=new LineDataSet(dataValues1,"");
        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(lineDataSet);
        //CUSTOMIZAR LA LINEA (linechart)
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextSize(10);
        lineDataSet.setValueTextColor(Color.WHITE);
        //SETTEAR LOS DATOS A LA GRAFICA (linechart)
        LineData data=new LineData(dataSets);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return super.getFormattedValue(value)+" lts.";
            }
        });
        //DATOS DE DESCRIPCION (linechart)
        Description desc=new Description();
        desc.setText("");
        desc.setTextColor(Color.WHITE);
        waterQtChart.setDescription(desc);
        waterQtChart.setNoDataText("No hay datos aun");
        waterQtChart.getAxisRight().setEnabled(false);
        waterQtChart.getAxisLeft().setTextColor(Color.WHITE);
        waterQtChart.getXAxis().setTextColor(Color.WHITE);
        waterQtChart.getXAxis().setGridColor(Color.WHITE);
        waterQtChart.getXAxis().setLabelRotationAngle(30);
        waterQtChart.getXAxis().setLabelCount(dataVal.size()-1);
        waterQtChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        waterQtChart.getAxisLeft().setGridColor(Color.WHITE);
        waterQtChart.animateY(1500);
        waterQtChart.getLegend().setEnabled(false);
        XAxis xlabels=waterQtChart.getXAxis();
        xlabels.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String label="";
                if(lineChartLabels.size()==1){
                    if((int)value==-1||(int)value==1){
                        label="-";
                    }else{
                        label=lineChartLabels.get((int)value);
                    }
                }else{
                    label=lineChartLabels.get((int)value);
                }
                return label;
            }
        });
        waterQtChart.setData(data);
        waterQtChart.invalidate();

    }
    //BARCHART DATA SET
    private ArrayList<BarEntry> dataValuesBar(){
        dataBarVal.add(new BarEntry(0,2));
        dataBarVal.add(new BarEntry(1,5));
        dataBarVal.add(new BarEntry(2,4));
        dataBarVal.add(new BarEntry(3,7));
        dataBarVal.add(new BarEntry(4,6));
        dataBarVal.add(new BarEntry(5,5));
        dataBarVal.add(new BarEntry(6,4));
        dataBarVal.add(new BarEntry(7,6));
        return dataBarVal;
    }
    public void initBarData(){
        BarDataSet barDataSet=new BarDataSet(dataValuesBar(), "Variación de ph");
        BarData barData=new BarData();
        barData.addDataSet(barDataSet);
        barDataSet.setColor(getResources().getColor(R.color.brand_Color));
        phBarChart.setData(barData);
        //phBarChart.getAxisRight().setEnabled(false);
        phBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        phBarChart.animateY(1500);
        phBarChart.getLegend().setEnabled(false);
        Description desc=new Description();
        desc.setText("");
        phBarChart.setDescription(desc);
        phBarChart.invalidate();
    }
    //CARGA DE DATOS FIREBASE
    public void initFirebaseData(){
        //DATO DE ESTADO DE BOMBA
        motorReference.child("BOMBA_STATUS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 bombaStatus=snapshot.getValue(Integer.class);
                if(bombaStatus==1){
                    setMotorCardColor(bombaStatus);
                }else if(bombaStatus==0){
                    setMotorCardColor(bombaStatus);
                }else{
                    Toast.makeText(MainActivity.this, "Error inesperado-snapshotVal: "+bombaStatus, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        cardsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int solids=snapshot.child("SOLIDS_QTY").getValue(Integer.class);
                txtSolids.setText(solids+"mg/L");
                int level=snapshot.child("WATER_LEVEL").getValue(Integer.class);
                txtLevel.setText(level+"%");
                int ph=snapshot.child("PH_LEVEL").getValue(Integer.class);
                txtPh.setText("Valor: "+ph);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setMotorCardColor(int status){
        if(status==1){
            motorCardBlue.animate().alpha(1f).setDuration(1500);
            motorCardBlue.setVisibility(View.VISIBLE);
            motorCardRed.animate().alpha(0f).setDuration(1500);
            motorCardRed.setVisibility(View.GONE);
            motorCardParent.setBackground(getResources().getDrawable(R.drawable.card_transp_blue));

        }else{
            motorCardParent.setBackground(getResources().getDrawable(R.drawable.card_transp_red));
            motorCardRed.animate().alpha(1f).setDuration(1500);
            motorCardRed.setVisibility(View.VISIBLE);
            motorCardBlue.animate().alpha(0f).setDuration(1500);
            motorCardBlue.setVisibility(View.GONE);
        }

    }
    //ENCENDER/APAGAR BOMBA
    public void operarBomba(){
        if(bombaStatus==1){
            motorReference.child("BOMBA_STATUS").setValue(0).addOnCompleteListener(task -> {
                if(task.isComplete()){
                    setMotorCardColor(0);
                }
            });
        }else if(bombaStatus==0){
            motorReference.child("BOMBA_STATUS").setValue(1).addOnCompleteListener(task -> {
                if(task.isComplete()){
                    setMotorCardColor(1);
                }
            });
        }
    }
}