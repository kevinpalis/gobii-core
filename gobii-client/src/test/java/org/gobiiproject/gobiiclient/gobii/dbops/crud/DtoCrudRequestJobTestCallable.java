package org.gobiiproject.gobiiclient.gobii.dbops.crud;

import org.gobiiproject.gobiiapimodel.payload.Header;
import org.gobiiproject.gobiiapimodel.payload.HeaderStatusMessage;
import org.gobiiproject.gobiiapimodel.payload.PayloadEnvelope;
import org.gobiiproject.gobiiapimodel.restresources.common.RestUri;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContext;
import org.gobiiproject.gobiiclient.core.gobii.GobiiClientContextAuth;
import org.gobiiproject.gobiiclient.core.gobii.GobiiEnvelopeRestResource;
import org.gobiiproject.gobiimodel.config.RestResourceId;
import org.gobiiproject.gobiimodel.dto.noaudit.JobDTO;
import org.gobiiproject.gobiimodel.types.GobiiProcessType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.junit.Assert;

import java.util.concurrent.Callable;

/**
 * Created by VCalaminos on 8/25/2017.
 */
public class DtoCrudRequestJobTestCallable implements Callable<Object> {

    private JobDTO jobDTO = null;

    public DtoCrudRequestJobTestCallable(JobDTO jobDTO) throws Exception {
        this.jobDTO = jobDTO;
        Assert.assertTrue(GobiiClientContextAuth.authenticate());
    }


    @Override
    public Object call() throws Exception {

        String message = null;

        String jobName = this.jobDTO.getJobName();
        //System.out.println("Updating job: " + jobName);

        String newMessage = "new message";
        jobDTO.setMessage(newMessage);

        RestUri restUriStatusForGetById = GobiiClientContext.getInstance(null, false)
                .getUriFactory()
                .resourceByUriIdParamName("jobName", RestResourceId.GOBII_JOB);
        restUriStatusForGetById.setParamValue("jobName", jobName);

        PayloadEnvelope<JobDTO> postRequestEnvelope = new PayloadEnvelope<>(jobDTO, GobiiProcessType.UPDATE);
        GobiiEnvelopeRestResource<JobDTO, JobDTO> gobiiEnvelopeRestResourceGetById = new GobiiEnvelopeRestResource<>(restUriStatusForGetById);
        PayloadEnvelope<JobDTO> resultEnvelope = gobiiEnvelopeRestResourceGetById
                .put(JobDTO.class, postRequestEnvelope);

        Header header = resultEnvelope.getHeader();
        if (!header.getStatus().isSucceeded() ||
                header
                        .getStatus()
                        .getStatusMessages()
                        .stream()
                        .filter(headerStatusMessage -> headerStatusMessage.getGobiiStatusLevel().equals(GobiiStatusLevel.VALIDATION))
                        .count() > 0) {
            message = "*** Header errors: ";
            for (HeaderStatusMessage currentStatusMesage : header.getStatus().getStatusMessages()) {
                message += currentStatusMesage.getMessage();
            }
        } else {

            JobDTO jobDtoRetrieved = resultEnvelope.getPayload().getData().get(0);
            if (!jobDtoRetrieved.getJobName().equals(jobName)) {
                message = "The job name of the DTO did not match that of the URI parameter: " + jobName;
            }

            if (!jobDtoRetrieved.getMessage().equals(newMessage)) {
                message = "The message field of the retrieved DTO " + jobDtoRetrieved.getMessage()
                        + " did not match the message it should have been updated to : " + newMessage;
            }
        }

        return message;
    }

}
