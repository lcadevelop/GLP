package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CompraDao
{
    @Query("SELECT * FROM compra")
    List<Compra> getAll();

    @Query("SELECT * FROM compra WHERE idcompra IN (:compraIds)")
    List<Compra> loadAllByIds(Integer[] compraIds);

    @Insert
    void insertAll(Compra... compras);

    @Delete
    void delete(Compra compra);
}
