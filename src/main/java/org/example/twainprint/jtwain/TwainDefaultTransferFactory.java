/*
 * Copyright 2018 (c) Denis Andreev (lucifer).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.twainprint.jtwain;


import org.example.twainprint.jtwain.transfer.TwainFileTransfer;
import org.example.twainprint.jtwain.transfer.TwainMemoryTransfer;
import org.example.twainprint.jtwain.transfer.TwainNativeTransfer;
import org.example.twainprint.jtwain.transfer.TwainTransfer;

/**
 *
 * @author lucifer
 */
public class TwainDefaultTransferFactory implements TwainTransferFactory {

    public TwainDefaultTransferFactory() {
    }

    @Override
    public TwainTransfer createMemoryTransfer(TwainSource source) {
        return new TwainMemoryTransfer(source);
    }

    @Override
    public TwainTransfer createNativeTransfer(TwainSource source) {
        return new TwainNativeTransfer(source);
    }

    @Override
    public TwainTransfer createFileTransfer(TwainSource source) {
        return new TwainFileTransfer(source);
    }

}
