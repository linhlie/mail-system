package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name = "Fetch_Mail_Error_Logs")
public class FetchMailError {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Date createdAt;

    private String errorLog;

    public FetchMailError() {
    }

    public FetchMailError(long id) {
        this.id = id;
    }

    public FetchMailError(Date createdAt, String errorLog) {
        this.createdAt = createdAt;
        this.errorLog = errorLog;
    }
}
