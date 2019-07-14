package br.com.bspicinini.drwe.processor;

import br.com.bspicinini.drwe.model.destination.UserDestination;
import br.com.bspicinini.drwe.model.origin.UserOrigin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class UserProcessor implements ItemProcessor<UserOrigin, UserDestination> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProcessor.class);

    @Override
    public UserDestination process(final UserOrigin userOrigin) throws Exception {

        UserDestination userDestination = new UserDestination(
                userOrigin.getUserName(),
                userOrigin.getFirstName(),
                userOrigin.getLastName(),
                userOrigin.getGender(),
                userOrigin.getPassword(),
                userOrigin.getStatus());


        LOGGER.info("Converting (" + userOrigin + ") into (" + userDestination + ")");

        return userDestination;
    }
}
