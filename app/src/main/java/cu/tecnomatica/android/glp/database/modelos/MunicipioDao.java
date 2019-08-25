package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MunicipioDao
{
    @Query("SELECT * FROM municipio")
    List<Municipio> getAll();

    @Query("SELECT * FROM municipio WHERE idmunicipio IN (:municipioIds)")
    List<Municipio> loadAllByIds(Integer[] municipioIds);

    @Insert
    void insertAll(Municipio... municipios);

    @Delete
    void delete(Municipio municipio);
}
