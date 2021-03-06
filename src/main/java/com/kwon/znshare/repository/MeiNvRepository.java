package com.kwon.znshare.repository;

import com.kwon.znshare.entity.MeiNv;
import com.kwon.znshare.vo.CommonVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeiNvRepository extends JpaRepository<MeiNv, Long> {

    /**
     * 全部查询
     * @param page
     * @param pageSize
     * @return
     */
    @Query(value = "SELECT * FROM mei_nv m GROUP BY m.fragment ORDER BY m.creat_time DESC LIMIT ?1,?2", nativeQuery = true)
    List<MeiNv> getMeiNvAll(int page, int pageSize);

    /**
     * 按类型查询
     * @param page
     * @param pageSize
     * @param type
     * @return
     */
    @Query(value = "SELECT * FROM mei_nv m WHERE m.type = ?3 ORDER BY m.creat_time DESC LIMIT ?1,?2", nativeQuery = true)
    List<MeiNv> getMeiNvByTpye(int page, int pageSize, String type);
}