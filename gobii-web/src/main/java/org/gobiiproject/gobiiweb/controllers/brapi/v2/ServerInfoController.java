package org.gobiiproject.gobiiweb.controllers.brapi.v2;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiibrapi.calls.calls.BrapiResponseMapCalls;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiMasterPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.BrApiServerInfoPayload;
import org.gobiiproject.gobiimodel.dto.brapi.envelope.ErrorPayload;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Brapi REST endpoint for serverinfo
 *
 * @author vg249
 */
@Scope(value = "request")
@Controller
@RequestMapping("/crops/{cropType}/brapi/v2/serverinfo")
@CrossOrigin
@Api
@Slf4j
public class ServerInfoController {

    /**
     * List all BrApi compliant web services in GDM system
     *
     * @param request - request object
     * @return Json object with list of brapi calls in GDM
     * @throws GobiiException
     */
    @GetMapping(produces = "application/json")
    @ApiOperation(
        value = "Get ServerInfo", notes = "List of all calls",
        tags = {"ServerInfo"}, extensions = {
        @Extension(properties = {
            @ExtensionProperty(name="summary", value="ServerInfo"),
        })
    }
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "", response = BrapiResponseMapCalls.class),
        @ApiResponse(code = 500, message = "", response = ErrorPayload.class)
    })
    @ResponseBody
    public ResponseEntity<BrApiMasterPayload> getServerInfo(HttpServletRequest request
    ) throws GobiiException {

        BrapiResponseMapCalls brapiResponseServerInfos = new BrapiResponseMapCalls(request);

        BrApiServerInfoPayload serverInfoPayload = new BrApiServerInfoPayload(
            brapiResponseServerInfos.getBrapi2ServerInfos());

        return ResponseEntity.ok(serverInfoPayload);
    }

}
