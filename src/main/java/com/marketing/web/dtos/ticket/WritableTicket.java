package com.marketing.web.dtos.ticket;

import com.marketing.web.enums.ImportanceLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableTicket implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String title;

    @NotBlank(message = "{validation.notBlank}")
    private String message;

    @NotNull(message = "{validation.notNull}")
    private ImportanceLevel importanceLevel;
}
