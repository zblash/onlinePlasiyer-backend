package com.marketing.web.dtos.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritableTicketReply implements Serializable {

    @NotBlank(message = "{validation.notBlank}")
    private String message;

}
