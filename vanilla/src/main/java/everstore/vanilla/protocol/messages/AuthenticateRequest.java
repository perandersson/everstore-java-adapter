package everstore.vanilla.protocol.messages;

import everstore.vanilla.HeaderProperties;
import everstore.vanilla.RequestUID;
import everstore.vanilla.WorkerUID;
import everstore.vanilla.io.EndianAwareOutputStream;
import everstore.vanilla.protocol.DataStoreRequest;
import everstore.vanilla.protocol.Header;
import everstore.vanilla.protocol.MessageRequest;

import java.io.IOException;

import static everstore.vanilla.protocol.Constants.INTEGER;
import static everstore.vanilla.protocol.RequestType.AUTHENTICATE;

public final class AuthenticateRequest implements MessageRequest {
    public final String username;
    public final String password;

    public AuthenticateRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void write(EndianAwareOutputStream stream) throws IOException {
        stream.putInt(username.length());
        stream.putInt(password.length());
        stream.putString(username);
        stream.putString(password);
    }

    @Override
    public int size() {
        return INTEGER * 2 + username.length() + password.length();
    }

    public static DataStoreRequest create(String username, String password) {
        final AuthenticateRequest request = new AuthenticateRequest(username, password);
        final Header header = new Header(AUTHENTICATE, request.size(), RequestUID.ZERO,
                HeaderProperties.NONE, WorkerUID.ZERO);
        return new DataStoreRequest(header, request);
    }
}
