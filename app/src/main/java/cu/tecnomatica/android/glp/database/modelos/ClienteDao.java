package cu.tecnomatica.android.glp.database.modelos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClienteDao
{
    @Query("SELECT * FROM cliente")
    List<Cliente> getAll();

    @Query("SELECT * FROM cliente WHERE idcliente IN (:clientesIDs)")
    List<Cliente> loadAllByIds(Integer[] clientesIDs);

    @Insert
    void insertAll(Cliente... clientes);

    @Delete
    void delete(Cliente cliente);
}
