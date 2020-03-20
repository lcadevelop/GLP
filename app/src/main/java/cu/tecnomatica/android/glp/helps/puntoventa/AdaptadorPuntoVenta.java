package cu.tecnomatica.android.glp.helps.puntoventa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import cu.tecnomatica.android.glp.R;
import cu.tecnomatica.android.glp.activities.localizacion.MapaActivity;

public class AdaptadorPuntoVenta extends RecyclerView.Adapter<AdaptadorPuntoVenta.PuntosVentaViewHolder> implements View.OnClickListener
{
    private ArrayList<PuntoVentaHelp> puntosdeventa;
    private View.OnClickListener onClickListener;

    public AdaptadorPuntoVenta(ArrayList<PuntoVentaHelp> puntosdeventa)
    {
        this.puntosdeventa = puntosdeventa;
    }

    @Override
    public AdaptadorPuntoVenta.PuntosVentaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_punto_venta,viewGroup, false);
        itemView.setOnClickListener(this);
        AdaptadorPuntoVenta.PuntosVentaViewHolder puntoventaViewHolder = new AdaptadorPuntoVenta.PuntosVentaViewHolder(itemView);
        return puntoventaViewHolder;
    }

    public void setOnClickListener(View.OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View view)
    {
        if (onClickListener != null)
        {
            onClickListener.onClick(view);
        }
    }

    @Override
    public void onBindViewHolder(AdaptadorPuntoVenta.PuntosVentaViewHolder viewHolder, int pos)
    {
        PuntoVentaHelp item = puntosdeventa.get(pos);
        viewHolder.BindPuntoVenta(item);
    }

    @Override
    public int getItemCount()
    {
        return puntosdeventa.size();
    }

    public static class PuntosVentaViewHolder extends RecyclerView.ViewHolder
    {
        private TextView direccion;
        private TextView horario;
        private TextView telefono;
        private TextView mapa;

        public PuntosVentaViewHolder(final View itemView)
        {
            super(itemView);
            direccion = (TextView) itemView.findViewById(R.id.id_direccion_texto_pt);
            horario = (TextView) itemView.findViewById(R.id.id_horario_texto_pt);
            telefono = (TextView) itemView.findViewById(R.id.id_telefono_texto_pt);
            mapa = (TextView)itemView.findViewById(R.id.id_mapa_texto_pv);
        }

        public void BindPuntoVenta(PuntoVentaHelp puntoVentaHelp)
        {
            direccion.setText( puntoVentaHelp.getDireccion());
            horario.setText( puntoVentaHelp.getHorario());
            telefono.setText( puntoVentaHelp.getTelefono());
        }


    }
}
