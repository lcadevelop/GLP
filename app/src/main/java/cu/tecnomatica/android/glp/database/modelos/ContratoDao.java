package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContratoDao
{
    @Query("SELECT * FROM contrato")
    List<Contrato> getAll();

    @Query("SELECT * FROM contrato WHERE idcontrato IN (:contratoIds)")
    List<Contrato> loadAllByIds(Integer[] contratoIds);

    @Insert
    void insertAll(Contrato... contratoes);

    @Delete
    void delete(Contrato contrato);
}
