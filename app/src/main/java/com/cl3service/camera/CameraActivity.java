package com.cl3service.camera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cl3service.camera.Service.ServiceCon;
import com.cl3service.camera.Service.ServiceSocket;
import com.cl3service.camera.ShapePack.ShapeDados;
import com.cl3service.camera.preferences.PropPreference;
import com.cl3service.camera.tools.DecimalDigitsInputFilter;
import com.cl3service.camera.tools.DoMethodAfterExecute;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class CameraActivity extends AppCompatActivity{
    public static boolean flag_contaGota = false;
    private LayoutScreen layoutScreen;
    private ServiceCon con;
    public static boolean opt_config = true;
    public ContextThemeWrapper ctw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
        con = new ServiceCon(this);
        bindService(new Intent(this, ServiceSocket.class), con, Context.BIND_AUTO_CREATE);
        inicializaLayout();

        }catch(Exception e){
            e.printStackTrace();
            writeToFile(e, this);
        }
    }

    private void inicializaLayout(){
        ctw = new ContextThemeWrapper(this, R.style.DialogTheme);
        layoutScreen = new LayoutScreen(this);
        setContentView(layoutScreen);

        if(opt_config)
            change_to_config();
        else
            change_to_analise();
    }

    public void inicializaImagem(){
        try{
            if(con.service.dados != null){
                if(layoutScreen != null){
                    layoutScreen.setShape(1, con.service.dados);
                    if(con.service.dados.saveDados)
                        saveAndUpdateImg();
                    else if(con.service.dados.updateImg)
                        click_atualizarImg(null);
                }
            }

        }catch(Exception e){
            writeToFile(e, this);
            e.printStackTrace();
        }
    }

    public void updateImg(){
        layoutScreen.newImage();
        Toast.makeText(this,"Imagem Atualizada",Toast.LENGTH_LONG).show();
    }

    public void click_atualizarImg(View v){
        layoutScreen.hideFab();
        con.service.execute( ServiceSocket.NameTask.GET_IMAGE, CameraActivity.opt_config ? "config": "analise");
    }
    public void click_salvar(View v){
        layoutScreen.hideFab();
        if(opt_config)
            con.service.dados.set_backup();
        con.service.execute( ServiceSocket.NameTask.UPDATE_DADOS);
    }
    public void click_contaGota(View v){
        layoutScreen.hideFab();
        flag_contaGota = true;
        layoutScreen.setEventoContaGota();
        Toast.makeText(this,"Clique em cima da cor",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        MenuItem itemConfig = menu.findItem(R.id.action_config);
        MenuItem itemAlt = menu.findItem(R.id.action_alt);
        MenuItem itemAnalise = menu.findItem(R.id.action_analise);
        MenuItem itemProp = menu.findItem(R.id.action_prop);
        itemConfig.setVisible(!opt_config);
        itemAlt.setVisible(!opt_config);
        itemAnalise.setVisible(opt_config);
        itemProp.setVisible(opt_config);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_alt:
                if(con.service.dados.imgType == ShapeDados.TypeImg.ImgMask)
                    con.service.dados.imgType = ShapeDados.TypeImg.ImgOrig;
                else
                    con.service.dados.imgType = ShapeDados.TypeImg.ImgMask;

                layoutScreen.newImage();
                break;
            case R.id.action_analise:
                layoutScreen.delEventos();
                con.service.dados.set_backup();
                con.service.dados.cor_x = -1;
                con.service.dados.cor_y = -1;
                change_to_analise();
                saveAndUpdateImg();
                break;
            case R.id.action_config:
                con.service.dados.imgType = ShapeDados.TypeImg.ImgOrig;
                layoutScreen.setConfig();
                con.service.dados.cor_x = -1;
                con.service.dados.cor_y = -1;
                change_to_config();
                saveAndUpdateImg();
                break;
            case R.id.action_prop:
                Intent intent = new Intent(this, PropPreference.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void change_to_analise(){
        opt_config = false;
        getSupportActionBar().setTitle("Analisar Imagem");
        enable_buttonFab(false);
        invalidateOptionsMenu();
    }

    private void change_to_config(){
        opt_config = true;
        getSupportActionBar().setTitle("Configuração");
        enable_buttonFab(true);
        invalidateOptionsMenu();
    }

    private void saveAndUpdateImg(){
        layoutScreen.hideFab();
        try {
            Method method = con.service.getClass().getMethod("execute", ServiceSocket.NameTask.class, String.class);
            con.service.execute( ServiceSocket.NameTask.UPDATE_DADOS,
                    new DoMethodAfterExecute(con.service, method, ServiceSocket.NameTask.GET_IMAGE, CameraActivity.opt_config ? "config": "analise") );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void enable_buttonFab(boolean status){
        FloatingActionButton fBut = (FloatingActionButton)findViewById(R.id.size_area);
        FloatingActionButton fBut2 = (FloatingActionButton)findViewById(R.id.conta_gota);
        FloatingActionButton fBut3 = (FloatingActionButton)findViewById(R.id.salvar);
        if(status){
            fBut.setVisibility(View.VISIBLE);
            fBut.setEnabled(true);
            fBut2.setVisibility(View.VISIBLE);
            fBut2.setEnabled(true);
            fBut3.setVisibility(View.VISIBLE);
            fBut3.setEnabled(true);
        }else{
            fBut.setVisibility(View.GONE);
            fBut.setEnabled(false);
            fBut2.setVisibility(View.GONE);
            fBut2.setEnabled(false);
            fBut3.setVisibility(View.GONE);
            fBut3.setEnabled(false);
        }
    }

    private void writeToFile(Exception data, Context context){
        File path = new File("/storage/emulated/0/Download");
        File file = new File(path, "logg.txt");

        try {
            PrintStream ps = new PrintStream(file);
            data.printStackTrace(ps);
            ps.close();
        } catch (IOException e) {
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void click_areaSize(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);

        final View view = getLayoutInflater().inflate( R.layout.dialog_area_seguranca, null);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioAreaSize) ;
        final EditText editLargura = (EditText) view.findViewById(R.id.editAreaLargura) ;
        final EditText editAltura = (EditText) view.findViewById(R.id.editAreaAltura) ;

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId == R.id.radioAreaManual){
                    view.findViewById(R.id.lnLargura).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.lnComprimento).setVisibility(View.VISIBLE);
                }else{
                    view.findViewById(R.id.lnLargura).setVisibility(View.GONE);
                    view.findViewById(R.id.lnComprimento).setVisibility(View.GONE);
                }
            }

        });

        radioGroup.check(getIdCheckRadio());

        ShapeDados d = layoutScreen.getDados();
        float minLargura = d.convertToCm( d.min_width );
        float maxLargura = d.convertToCm( d.limit_width);

        editLargura.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,2)});
        editAltura.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3,2)});

        editLargura.setText( String.format("%.2f", d.convertToCm(d.width) ).replace(",", ".") );
        editAltura.setText( String.format("%.2f", d.convertToCm(d.height) ).replace(",", ".") );

        TextView textSizeMinimo = (TextView) view.findViewById(R.id.textSizeMinimo);
        TextView textSizeMaximo = (TextView) view.findViewById(R.id.textSizeMaximo);
        //textLarguraRange.setText("Largura: min ("+ String.format("%.2f", minLargura) +"cm) max ("+ String.format("%.2f", maxLargura) +"cm)");
        //textAlturaRange.setText("Comprimento: min ("+ String.format("%.2f", minAltura) +"cm) max ("+ String.format("%.2f", maxAltura) +"cm)");

        textSizeMinimo.setText("Tamanho mínimo: "+ String.format("%.2f", minLargura) +" cm");
        textSizeMaximo.setText("Tamanho máximo: "+ String.format("%.2f", maxLargura) +" cm");

        final AlertDialog dialog = builder.setCancelable(false)
                .setTitle("Tamanho da Área de Segurança")
                .setView(view)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancelar",null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float widthCm = 0, heightCm = 0;
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.radioAreaManual:
                                widthCm = Float.parseFloat( editLargura.getText().toString() );
                                heightCm = Float.parseFloat( editAltura.getText().toString() );
                                break;
                            case R.id.radioArea10x10:
                                widthCm = 10f;
                                heightCm = 10f;
                                break;
                            case R.id.radioArea15x15:
                                widthCm = 15f;
                                heightCm = 15f;
                                break;
                            case R.id.radioArea20x20:
                                widthCm = 20f;
                                heightCm = 20f;
                                break;
                        }

                        if(validateAreaSize(widthCm, heightCm)){
                            layoutScreen.setAreaAtCm(widthCm, heightCm);
                            dialog.dismiss();
                        }
                    }

                });
            }
        });

        layoutScreen.hideFab();
        dialog.show();
    }

    private boolean validateAreaSize(float width, float height){
        ShapeDados d = layoutScreen.getDados();
        float minLargura = d.convertToCm(d.min_width);
        float maxLargura = d.convertToCm(d.limit_width);
        float minComprimento  = d.convertToCm(d.min_height);
        float maxComprimento  = d.convertToCm(d.limit_height);

        if( width > maxLargura ){
            Toast.makeText(this,"Largura maior que o limite máximo: " + String.format("%.2f", maxLargura), Toast.LENGTH_LONG ).show();
            return false;
        } else
        if( width < minLargura ){
            Toast.makeText(this,"Largura menor que o limite mínimo: " + String.format("%.2f", minLargura), Toast.LENGTH_LONG ).show();
            return false;
        }

        if( height > maxComprimento ){
            Toast.makeText(this,"Comprimento maior que o limite máximo: " + String.format("%.2f", maxComprimento), Toast.LENGTH_LONG ).show();
            return false;
        } else
        if( height < minComprimento ){
            Toast.makeText(this,"Comprimento menor que o limite mínimo: " + String.format("%.2f", minComprimento), Toast.LENGTH_LONG ).show();
            return false;
        }

        return true;
    }

    private int getIdCheckRadio(){
        ShapeDados d = layoutScreen.getDados();
        float widthCm = d.convertToCm(d.width);
        float heightCm = d.convertToCm(d.height);

        if(widthCm == 10f && heightCm == 10f)
            return R.id.radioArea10x10;
        if(widthCm == 15f && heightCm == 15f)
            return R.id.radioArea15x15;
        if(widthCm == 20f && heightCm == 20f)
            return R.id.radioArea20x20;

        return R.id.radioAreaManual;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(con.service != null) con.service.setActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(con);
    }
}
