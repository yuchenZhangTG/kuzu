﻿/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/TokensStartState.h"

using namespace antlr4::atn;

size_t TokensStartState::getStateType() {
    return TOKEN_START;
}
