package com.vaccine.service;

import com.vaccine.common.UserContext;
import com.vaccine.entity.Appointment;
import com.vaccine.exception.BusinessException;
import com.vaccine.mapper.AppointmentMapper;
import com.vaccine.mapper.VaccineMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AppointmentServiceTest {

    private AppointmentService appointmentService;
    private AppointmentMapper appointmentMapper;
    private VaccineMapper vaccineMapper;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService();
        appointmentMapper = mock(AppointmentMapper.class);
        vaccineMapper = mock(VaccineMapper.class);
        ReflectionTestUtils.setField(appointmentService, "appointmentMapper", appointmentMapper);
        ReflectionTestUtils.setField(appointmentService, "vaccineMapper", vaccineMapper);
    }

    @AfterEach
    void clearUserContext() {
        UserContext.remove();
    }

    @Test
    void doesNotRestoreStockAgainForARejectedAppointment() {
        UserContext.set(100L, "user", "user");
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setUserId(100L);
        appointment.setStatus(2);
        when(appointmentMapper.findById(1L)).thenReturn(appointment);

        assertThatThrownBy(() -> appointmentService.cancel(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存已自动恢复");

        verify(appointmentMapper).findById(1L);
        verifyNoInteractions(vaccineMapper);
    }
}
