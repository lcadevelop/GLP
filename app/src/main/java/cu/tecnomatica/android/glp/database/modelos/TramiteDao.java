package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TramiteDao
{
    @Query("SELECT * FROM tramite")
    List<Tramite> getAll();

    @Query("SELECT * FROM tramite WHERE idtramite IN (:tramiteIds)")
    List<Tramite> loadAllByIds(Integer[] tramiteIds);

    @Insert
    void insertAll(Tramite... tramites);

    @Delete
    void delete(Tramite tramite);
}
