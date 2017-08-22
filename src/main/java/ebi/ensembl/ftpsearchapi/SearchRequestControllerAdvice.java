package ebi.ensembl.ftpsearchapi;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 *  Overrides way of throwing exceptions in HTTP response.
 */
@ControllerAdvice(basePackageClasses = SearchRequestController.class)
public class SearchRequestControllerAdvice extends ResponseEntityExceptionHandler{

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    ResponseEntity<String> handleControllerException(HttpServletRequest request, Throwable ex) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<String>("Something went wrong: " + ex.getMessage() + ".",
                status);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

}
