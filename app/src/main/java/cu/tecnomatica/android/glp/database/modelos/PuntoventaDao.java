package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PuntoventaDao
{
    @Query("SELECT * FROM puntoventa")
    List<Puntoventa> getAll();

    @Query("SELECT * FROM puntoventa WHERE idpuntoventa IN (:puntoventaIds)")
    List<Puntoventa> loadAllByIds(Integer[] puntoventaIds);

    @Insert
    void insertAll(Puntoventa... puntoventas);

    @Delete
    void delete(Puntoventa puntoventa);
}
