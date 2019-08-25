package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ServicioDao
{
    @Query("SELECT * FROM servicio")
    List<Servicio> getAll();

    @Query("SELECT * FROM servicio WHERE idservicio IN (:servicioIds)")
    List<Servicio> loadAllByIds(Integer[] servicioIds);

    @Insert
    void insertAll(Servicio... servicios);

    @Delete
    void delete(Servicio servicio);
}
