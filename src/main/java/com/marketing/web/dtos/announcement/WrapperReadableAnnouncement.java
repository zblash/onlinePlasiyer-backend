package com.marketing.web.dtos.announcement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrapperReadableAnnouncement implements Serializable {

    private List<ReadableAnnouncement> values;

    private int size;

}
