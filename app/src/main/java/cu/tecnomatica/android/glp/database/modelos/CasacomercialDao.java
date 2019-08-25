package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CasacomercialDao
{
    @Query("SELECT * FROM casacomercial")
    List<Casacomercial> getAll();

    @Query("SELECT * FROM casacomercial WHERE idcasacomercial IN (:casacomercialIDs)")
    List<Casacomercial> loadAllByIds(Integer[] casacomercialIDs);

    @Insert
    void insertAll(Casacomercial... casacomercials);

    @Delete
    void delete(Casacomercial casacomercial);
}
