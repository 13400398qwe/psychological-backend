package com.example.scupsychological.mapper;

import com.example.scupsychological.pojo.dto.AvailableSlotQueryDto;
import com.example.scupsychological.pojo.entity.ScheduleSlots;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 可预约时间段表 Mapper 接口
 * </p>
 *
 * @author tpj
 * @since 2025-06-24
 */
@Mapper
public interface ScheduleSlotsMapper extends BaseMapper<ScheduleSlots> {
    int insertBatch(@Param("slotList") List<ScheduleSlots> slotList);
    int atomicallyBookSlot(@Param("slotId") Long slotId);
    int physicallyDeleteExpiredAvailableSlots(@Param("now") LocalDateTime now);
    int atomicallyCancelBooking(@Param("slotId") Long slotId);
    /**
     * 【新增】自定义查询：获取所有状态为可用的【初访员】的号源列表
     * @param queryDto 包含筛选条件，如 staffId 或 queryDate
     * @return 符合条件的号源实体列表
     */
    List<ScheduleSlots> selectAvailableInterviewerSlots(@Param("query") AvailableSlotQueryDto queryDto);

    /**
     * 【新增】原子地将一个已预约的号源标记为已完成
     * @param slotId 要标记的号源ID
     * @return 数据库受影响的行数 (1 代表成功, 0 代表失败)
     */
    int markSlotAsCompleted(@Param("slotId") Long slotId);
    /**
     * 查询咨询师的号源
     */
    List<ScheduleSlots> selectAvailableCounselorSlots(@Param("query") AvailableSlotQueryDto queryDto);

    int atomicallyBookSlots(@Param("slotIds")List<Long> slotIdsToBook);
}
